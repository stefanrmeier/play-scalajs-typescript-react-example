/* Common Config */
const Merge = require('webpack-merge');
const CommonConfig = require('./webpack.common.config');

const BundleAnalyzerPlugin = require('webpack-bundle-analyzer').BundleAnalyzerPlugin;
const CompressionPlugin = require("compression-webpack-plugin");

const webpack = require('webpack');

module.exports = Merge.smart(CommonConfig, {
    entry: {
        examples: "./ts/examples/root.tsx",
        vendor: ["babel-polyfill", "react", "react-dom"]
    },

    plugins: [
        new webpack.optimize.UglifyJsPlugin({
            compress: {
                warnings: false
            }
        }),
        // If compressed, needs to activate gzip encoding: https://www.playframework.com/documentation/2.5.x/GzipEncoding
        // new CompressionPlugin({
        //     asset: "[path].gz[query]",
        //     deleteOriginalAsset: true,
        //     algorithm: "gzip",
        //     test: /\.(js|html)$/
        // }),
        new BundleAnalyzerPlugin({
            analyzerMode: 'static'
        })
    ],
});