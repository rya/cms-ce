package com.enonic.cms.api.client.model;

import com.enonic.cms.api.client.model.user.UserInfo;

public class CreateUserParams
        extends AbstractParams
    {
        private static final long serialVersionUID = 8835643065064629797L;

        public String userstore;

        public String username;

        public String password;

        public String displayName;

        public String email;

        public UserInfo userInfo = new UserInfo();
    }
