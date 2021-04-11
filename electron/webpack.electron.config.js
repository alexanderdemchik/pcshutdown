const path = require('path');
const { ContextReplacementPlugin } = require('webpack');
const { buildDirectory } = require('./package.json');
const CopyPlugin = require("copy-webpack-plugin");
const BUILD_DIRECTORY = buildDirectory;

module.exports = {
  resolve: {
    extensions: [".ts", ".tsx", ".js", ".json"]
  },
  devtool: 'source-map',
  entry: './src/index.ts',
  target: 'electron-main',
  module: {
    rules: [
      {
        test: /\.(ts|tsx)$/,
        exclude: /(node_modules)/,
        use: {
          loader: 'ts-loader',
          options: {
            transpileOnly: true
          }
        }
      },
    ],
  },
  output: {
    path: path.resolve(__dirname, BUILD_DIRECTORY),
    filename: 'index.js',
    clean: true
  },
  externals: [/node_modules/, 'bufferutil', 'utf-8-validate'],
  plugins: [
    new CopyPlugin({
      patterns: [
        { from: "src/assets", to: "assets" },
        { from: "src/locales", to: "locales" },
        { from: "src/config.json", to: "config.json" },
      ],
    }),
    new ContextReplacementPlugin(/any-promise/)
  ],
};