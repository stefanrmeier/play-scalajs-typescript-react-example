const path = require('path');
const jsonPath = path.resolve(__dirname, '../../tsconfig.json');

module.exports = {
  enforce: 'pre',
  test: /\.ts(x?)$/,
  include: [path.resolve(__dirname, '../../ts/')],
  use: 'awesome-typescript-loader?configFileName=' + jsonPath
};
