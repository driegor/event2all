package org.uoc.pfc.eventual.model.post;

import org.uoc.pfc.eventual.utils.enums.PostEnum;

public class Notification extends Post<String> {

    public Notification(String header) {
	super(header, PostEnum.NOTIFICATION);
    }

    public Notification(String header, String content) {
	super(header, content, PostEnum.NOTIFICATION);
    }

}
