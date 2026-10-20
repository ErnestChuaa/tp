from html.parser import HTMLParser
from pathlib import Path
from urllib.parse import urlsplit, unquote
import hashlib
import json
import sys
import xml.etree.ElementTree as ET

root = Path(sys.argv[1]).resolve()
site = root / 'docs/_site'
class Page(HTMLParser):
    def __init__(self, path):
        super().__init__()
        self.ids, self.links, self.tables = set(), [], []
        self.table, self.cells = None, None
        self.feed(path.read_text(encoding='utf-8'))
    def handle_starttag(self, tag, attrs):
        attrs = dict(attrs)
        if 'id' in attrs:
            self.ids.add(attrs['id'])
        for key in ('href', 'src'):
            if key in attrs:
                self.links.append(attrs[key])
        if tag == 'table':
            self.table = []
        if tag == 'tr' and self.table is not None:
            self.cells = 0
        if tag in ('td', 'th') and self.cells is not None:
            self.cells += int(attrs.get('colspan', 1))
    def handle_endtag(self, tag):
        if tag == 'tr' and self.cells is not None:
            self.table.append(self.cells)
            self.cells = None
        if tag == 'table':
            self.tables.append(self.table)
            self.table = None

pages = {p.resolve(): Page(p) for p in site.rglob('*.html')}
failures = []
checked = images = 0
for path, page in pages.items():
    for href in page.links:
        url = urlsplit(href)
        if url.scheme or url.netloc:
            continue
        dest = site / unquote(url.path).lstrip('/') if url.path.startswith('/') else path.parent / unquote(url.path)
        if not url.path:
            dest = path
        if dest.is_dir():
            dest /= 'index.html'
        checked += 1
        if not dest.exists():
            failures.append(f'{path.name}: missing {href}')
        elif url.fragment and dest.resolve() in pages and unquote(url.fragment) not in pages[dest.resolve()].ids:
            failures.append(f'{path.name}: missing fragment {href}')
        if dest.suffix.lower() in ('.png', '.svg', '.jpg'):
            images += 1
    for number, table in enumerate(page.tables, 1):
        if not table or len(set(table)) != 1:
            failures.append(f'{path.name}: table {number} has inconsistent columns')
results = [ET.parse(x).getroot() for x in (root/'build/test-results/test').glob('TEST-*.xml')]
jar = root/'build/libs/ponhub.jar'
report = {'source': '9d7ae406e1364bd5b39369545053fe6ec3273888',
          'rendered_pages': len(pages), 'local_targets': checked, 'images': images, 'link_table_failures': failures,
          'tests': {k: sum(int(r.get(k, 0)) for r in results) for k in ['tests','failures','errors','skipped']},
          'suites': len(results), 'jar_bytes': jar.stat().st_size, 'jar_sha256': hashlib.sha256(jar.read_bytes()).hexdigest()}
(Path(__file__).parent/'audit.json').write_text(json.dumps(report, indent=2)+'\n', encoding='utf-8')
print(json.dumps(report, indent=2))
assert not failures
assert report['tests']['failures'] == report['tests']['errors'] == 0
