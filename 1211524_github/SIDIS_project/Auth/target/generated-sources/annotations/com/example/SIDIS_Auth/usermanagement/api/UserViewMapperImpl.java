package com.example.SIDIS_Auth.usermanagement.api;

import com.example.SIDIS_Auth.usermanagement.model.User;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-11-06T16:52:31+0000",
    comments = "version: 1.5.3.Final, compiler: javac, environment: Java 21.0.4 (Oracle Corporation)"
)
@Component
public class UserViewMapperImpl extends UserViewMapper {

    @Override
    public UserView toUserView(User user) {
        if ( user == null ) {
            return null;
        }

        UserView userView = new UserView();

        if ( user.getId() != null ) {
            userView.setId( String.valueOf( user.getId() ) );
        }
        userView.setUsername( user.getUsername() );
        userView.setFullName( user.getFullName() );

        return userView;
    }

    @Override
    public List<UserView> toUserView(List<User> users) {
        if ( users == null ) {
            return null;
        }

        List<UserView> list = new ArrayList<UserView>( users.size() );
        for ( User user : users ) {
            list.add( toUserView( user ) );
        }

        return list;
    }
}
