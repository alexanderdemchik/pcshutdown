const env = require('dotenv').config().parsed;

module.exports = {
  "appId": "PC Shutdown",
  "win": {
    "target": "NSIS",
    "artifactName": "${productName}-${version}.${ext}",
    "icon": "src/assets/app_icon.ico"
  },
  "linux": {
    "target": ["tar.gz", "rpm", "deb"]
  },
  "nsis": {
    "oneClick": false,
    "allowToChangeInstallationDirectory": true,
  },
  "extraFiles": [
    {
      "from": `${env.BUILD_DIRECTORY}/locales`,
      "to": "locales",
      "filter": [
        "**/*"
      ]
    },
    {
      "from": `${env.BUILD_DIRECTORY}/assets`,
      "to": "assets",
      "filter": [
        "**/*"
      ]
    },
    {
      "from": `${env.BUILD_DIRECTORY}/config.json`,
      "to": "config.json",
      "filter": [
        "**/*"
      ]
    }
  ]
}