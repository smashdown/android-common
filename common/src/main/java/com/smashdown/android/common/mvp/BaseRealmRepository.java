package com.smashdown.android.common.mvp;

import io.realm.Realm;
import io.realm.RealmObject;

/**
 * 1) Basically, realm should be managed by the class who using it.
 * 2) All the RealmResult is managed object, so the managing of the result should be done by the class who using it.
 */
public abstract class BaseRealmRepository<T extends RealmObject> implements BaseRepository<T> {
    protected Realm realm;

    public BaseRealmRepository(Realm realm) {
        this.realm = realm;
    }

    public Realm getRealm() {
        return realm;
    }

    public void setRealm(Realm realm) {
        this.realm = realm;
    }
}