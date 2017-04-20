package com.neat.dagger;

import com.neat.view.CardEditActivity;

import dagger.Subcomponent;

/**
 * Created by f.gatti.gomez on 02/02/2017.
 */
@UserScope
@Subcomponent(modules = {UserModule.class})
public interface UserComponent {

    SessionComponent plus(SessionModule sessionModule);

    void inject(CardEditActivity cardEditActivity);

}
