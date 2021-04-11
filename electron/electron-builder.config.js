const { buildDirectory } = require('./package.json');

module.exports = {
  "appId": "PC Shutdown",
  "win": {
    "target": "NSIS",
    "artifactName": "${productName}-${version}.${ext}",
    "icon": "src/assets/app_icon.ico"
  },
  "linux": {
    "target": ["tar.gz", "rpm", "deb"],
    "icon": "src/assets/app_icon.icns"
  },
  "nsis": {
    "oneClick": false,
    "allowToChangeInstallationDirectory": true,
  },
  "extraFiles": [
    {
      "from": `${buildDirectory}/locales`,
      "to": "locales",
      "filter": [
        "**/*"
      ]
    },
    {
      "from": `${buildDirectory}/assets`,
      "to": "assets",
      "filter": [
        "**/*"
      ]
    },
    {
      "from": `${buildDirectory}/config.json`,
      "to": "config.json",
      "filter": [
        "**/*"
      ]
    }
  ]
}