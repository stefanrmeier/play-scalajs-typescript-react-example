const path = require('path');

module.exports = {
  enforce: 'pre',
  include: [path.resolve(__dirname, '../../packages/')],
  test: /\.ts(x?)$/,
  use: [ 'happypack/loader?id=tsLint' ]
};
