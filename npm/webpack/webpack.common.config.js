const WebpackShellPlugin = require('webpack-shell-plugin');
const TsConfigPathsPlugin = require('awesome-typescript-loader').TsConfigPathsPlugin;
const AtsLoaderConfig = require('./loaders-config/awesome-typescript-loader.config');
const BabelLoaderConfig = require('./loaders-config/babel-loader.config');
const TslintLoaderConfig = require('./loaders-config/tslint-loader.config');
const StylelintPluginConfig = require('./plugin-config/stylelint-plugin.config');

const ExtractTextPlugin = require('extract-text-webpack-plugin');
const extractLocalCss = new ExtractTextPlugin('assets/stylesheets/[name].css');
const extractVendorCss = new ExtractTextPlugin('assets/stylesheets/vendor-[name].css');

const os = require('os');
const webpack = require('webpack');
const HappyPack = require('happypack');
const happyThreadPool = HappyPack.ThreadPool({ size: os.cpus().length });
module.exports = {
    output: {
        filename: "[name].js",
        path: __dirname + "/../dist"
    },
    plugins: [
        // StylelintPluginConfig,
        new webpack.DefinePlugin({
            'process.env.NODE_ENV': JSON.stringify(process.env.NODE_ENV),
        }),
        new HappyPack({
            id: 'tsLint',
            threadPool: happyThreadPool,
            loaders: [{
                path: 'tslint-loader',
                loader: 'tslint-loader'
            }]
        }),
        new HappyPack({
            id: 'babel',
            threadPool: happyThreadPool,
            loaders: [{
                path: 'babel-loader',
                loader: 'babel-loader',
                query: {
                    presets: ["react", "es2015", "stage-0"],
                    // plugins: ["react-hot-loader/babel"],
                    cacheDirectory: true
                }
            }]
        }),
        new webpack.optimize.CommonsChunkPlugin({
            name: "vendor",

            // filename: "vendor.js"
            // (Give the chunk a different name)

            minChunks: Infinity,
            // (with more entries, this ensures that no other module
            //  goes into the vendor chunk)
        }),
        /* Vendor CSS are separately bundled as vendor-[name].css */
        extractVendorCss,

        /* CSS are separately bundled as [name].css */
        extractLocalCss,
        new WebpackShellPlugin({ 
            onBuildStart: [
                "echo Clean dist directory",
                "npm run clean",
            ], 
            onBuildExit: [
                "echo Ship code",
                "npm run ship:js"
            ]
        })
    ],
    // Enable sourcemaps for debugging webpack's output.
    devtool: "source-map",

    resolve: {
        extensions: [".webpack.js", ".web.js", ".ts", ".tsx", ".jsx", ".js"],
        plugins: [
            new TsConfigPathsPlugin()
        ]
    },

    module: {
        rules: [
            TslintLoaderConfig,
            AtsLoaderConfig,
            BabelLoaderConfig,
            {
                test: /\.scss$/,
                loader: extractLocalCss.extract({
                    fallback: 'style-loader',
                    use: ['css-loader?modules&importLoaders=1&-composes&localIdentName=[name]__[local]___[hash:base64:5]&minimize=true', 'postcss-loader?parser=postcss-scss']
                })
            },

            /*
             * CSS files (expects 3rd-party lib only)
             */
            {
                test: /\.css$/,
                loader: extractVendorCss.extract({
                    fallback: 'style-loader',
                    use: 'css-loader?minimize=true'
                })
            }

        ]
    },
};