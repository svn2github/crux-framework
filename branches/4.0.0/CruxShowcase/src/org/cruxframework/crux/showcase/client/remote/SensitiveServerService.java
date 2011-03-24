package org.cruxframework.crux.showcase.client.remote;

import org.cruxframework.crux.core.client.rpc.st.UseSynchronizerToken;

import com.google.gwt.user.client.rpc.RemoteService;

public interface SensitiveServerService extends RemoteService
{
	@UseSynchronizerToken
	String sensitiveMethod();
	
	@UseSynchronizerToken(blocksUserInteraction=false)
	String sensitiveMethodNoBlock();
}
