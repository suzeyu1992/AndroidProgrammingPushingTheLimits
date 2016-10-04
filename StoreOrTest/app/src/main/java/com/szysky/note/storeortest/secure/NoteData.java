package com.szysky.note.storeortest.secure;

import java.util.Date;
import java.util.UUID;

/**
 * Author :  suzeyu
 * Time   :  2016-10-04  下午4:02
 * Blog   :  http://szysky.com
 * GitHub :  https://github.com/suzeyu1992
 * ClassDescription :
 */

public class NoteData {

    private UUID mID;
    private String mTitle;
    private String mContent;
    private String mCategory;
    private Date mLastChanger;
    public NoteData(){
        mID = UUID.randomUUID();
    }

    public UUID getmID() {
        return mID;
    }

    public void setmID(UUID mID) {
        this.mID = mID;
    }

    public String getmTitle() {
        return mTitle;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getmContent() {
        return mContent;
    }

    public void setmContent(String mContent) {
        this.mContent = mContent;
    }

    public String getmCategory() {
        return mCategory;
    }

    public void setmCategory(String mCategory) {
        this.mCategory = mCategory;
    }

    public Date getmLastChanger() {
        return mLastChanger;
    }

    public void setmLastChanger(Date mLastChanger) {
        this.mLastChanger = mLastChanger;
    }
}
