const path = require('path');

module.exports = {
  test: /\.(ts|js)(x?)$/,
  include: [path.resolve(__dirname, '../../ts/')],
  use: [
      'happypack/loader?id=babel'
  ]
};
