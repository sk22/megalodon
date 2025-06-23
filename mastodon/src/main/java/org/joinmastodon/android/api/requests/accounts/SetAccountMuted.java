package org.joinmastodon.android.api.requests.accounts;

import org.joinmastodon.android.api.MastodonAPIRequest;
import org.joinmastodon.android.model.Relationship;

public class SetAccountMuted extends MastodonAPIRequest<Relationship>{
	public SetAccountMuted(String id, boolean muted, long duration, boolean muteNotifications){
		super(HttpMethod.POST, "/accounts/"+id+"/"+(muted ? "mute" : "unmute"), Relationship.class);
		if(muted)
			setRequestBody(new Request(duration, muteNotifications));
	}

	private static class Request{
		public long duration;
		public boolean muteNotifications;
		public Request(long duration, boolean muteNotifications){
			this.duration=duration;
			this.muteNotifications=muteNotifications;
		}
	}
}
