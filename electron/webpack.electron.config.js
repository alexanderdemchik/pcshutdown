const path = require('path');
const { DefinePlugin, ContextReplacementPlugin } = require('webpack');
const env = require('dotenv').config().parsed;
const CopyPlugin = require("copy-webpack-plugin");

const BUILD_DIRECTORY = env.BUILD_DIRECTORY;

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
  },
  plugins: [
    new DefinePlugin(env),
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