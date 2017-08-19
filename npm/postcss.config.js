module.exports = {
  plugins: [
    require('postcss-import')({
      resolve: ((id, dir, opts) => {
        if(!/\.(scss|css)$/.test(id)) {
          id = id + '.scss';
        }
        return require('postcss-import/lib/resolve-id')(id, dir, opts);
      })
    }),
    require('postcss-url')({ maxSize: 8 }),
    require('postcss-simple-vars'),
    require('postcss-color-function'),
    require('postcss-nested'),
    require('autoprefixer')
  ]
};
