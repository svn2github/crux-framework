package br.com.sysmap.crux.showcase.client.controller;

import java.util.ArrayList;

import br.com.sysmap.crux.core.client.controller.crossdoc.CrossDocument;

public interface BaseSourcesControllerCrossDoc extends CrossDocument {
	
	public void setSourceTabs(ArrayList<SourceTab> tabs);
	
}