package com.nitor.plantuml.lambda;

public enum DiagramType {

  IMAGE_PNG("image/png"), IMAGE_SVG_XML("image/svg+xml;charset=UTF-8"), TEXT_PLAIN("text/plain;charset=UTF-8"),
  IMAGEMAP("text/plain;charset=UTF-8");

  private String mimeType;

  DiagramType(String mimeType) {
    this.mimeType = mimeType;
  }

  public String getMimeType() {
    return mimeType;
  }

}
