//const { override, addBabelPresets} = require("customize-cra");

// module.exports = override(
//     ...addBabelPresets(["mobx"])
// );
const { override, addDecoratorsLegacy, disableEsLint } = require("customize-cra");

module.exports = override(
    addDecoratorsLegacy(),
    disableEsLint(),
);