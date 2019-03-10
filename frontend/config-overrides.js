const { override, addBabelPresets} = require("customize-cra");

module.exports = override(
    ...addBabelPresets(["mobx"])
);