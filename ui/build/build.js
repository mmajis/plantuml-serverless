const minify = require('html-minifier').minify;
const fs = require('fs');
const path = require('path');

const targetDir = 'dist';

var html = fs.readFileSync('index.html', 'utf8');
var result = minify(html, {
  removeAttributeQuotes: true,
  collapseWhitespace: true,
  conservativeCollapse: true,
  minifyCSS: true,
  minifyJS: true,
  removeComments: true
});
if (!fs.existsSync(targetDir)){
  fs.mkdirSync(targetDir);
}
fs.writeFileSync(targetDir + path.sep + 'index.html', result);