/** Called to slurp up all <meta> tags:
 * gwt:property, gwt:onPropertyErrorFn, gwt:onLoadErrorFn
 */
function processMetas() {
  var meta;
  var prefs = new $wnd.gadgets.Prefs();

  if (meta = prefs.getString('gwt:onLoadErrorFn')) {
    try {
      onLoadErrorFunc = eval(meta);
    } catch (e) {
      alert('Bad handler \"' + content + '\" for \"gwt:onLoadErrorFn\"');
    }
  }

  if (meta = prefs.getString('gwt:onPropertyErrorFn')) {
    try {
      propertyErrorFunc = eval(meta);
    } catch (e) {
      alert('Bad handler \"' + content +
        '\" for \"gwt:onPropertyErrorFn\"');
    }
  }

  if (meta = prefs.getArray('gwt:property')) {
    for (var i = 0; i < meta.length; i++) {
      var content = meta[i];
      if (content) {
        var value, eq = content.indexOf('=');
        if (eq >= 0) {
          name = content.substring(0, eq);
          value = content.substring(eq+1);
        } else {
          name = content;
          value = '';
        }
        metaProps[name] = value;
      }
    }
  }
}

