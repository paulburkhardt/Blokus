package boards;

/**
 * Enum storing the colors of the different GameThemes.
 *
 * @author pburkhar
 */
public enum GameTheme {
  NEON(new String[]{"#20df7f", "#48f0f0", "#b424e7", "#ffad00", "#093545"}, "/neonView.css"),
  ARCTIC(new String[]{"#003f60", "#477781", "#5c3866", "#637792", "#ECEFF8"}, "/arcticView.css"),
  TROPICAL(
      new String[]{"#ff4756", "#007b98", "#ff6429", "#476546", "#6ea601"}, "/tropicalView.css");

  private String[] colors;
  private String cssFile;

  public String[] getColors() {
    return colors;
  }

  public String getCssFile() {
    return cssFile;
  }

  GameTheme(String[] colors, String cssFile) {
    this.cssFile = cssFile;
    this.colors = colors;
  }
}
