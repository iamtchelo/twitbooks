const { override, addBabelPresets, fixBabelImports, addLessLoader } = require("customize-cra");

module.exports = override(
    fixBabelImports(
        'import', {
            libraryName: 'antd',
            libraryDirectory: 'es',
            style: true,
        }),
    addLessLoader({
        javascriptEnabled: true,
        modifyVars: {
            '@font-family': 'Raleway, sans-serif'
        },
    }),
    addBabelPresets(["mobx"]),
);
