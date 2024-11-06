package com.example.SIDIS_Auth.usermanagement.services;

import com.example.SIDIS_Auth.usermanagement.model.Role;
import com.example.SIDIS_Auth.usermanagement.model.User;
import java.util.Set;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-11-06T16:52:32+0000",
    comments = "version: 1.5.3.Final, compiler: javac, environment: Java 21.0.4 (Oracle Corporation)"
)
@Component
public class EditUserMapperImpl extends EditUserMapper {

    @Override
    public User create(CreateUserRequest request) {
        if ( request == null ) {
            return null;
        }

        String password = null;
        String username = null;

        password = request.getPassword();
        username = request.getUsername();

        User user = new User( username, password );

        if ( user.getAuthorities() != null ) {
            Set<Role> set = stringToRole( request.getAuthorities() );
            if ( set != null ) {
                user.getAuthorities().addAll( set );
            }
        }
        user.setFullName( request.getFullName() );

        return user;
    }

    @Override
    public void update(EditUserRequest request, User user) {
        if ( request == null ) {
            return;
        }

        if ( user.getAuthorities() != null ) {
            user.getAuthorities().clear();
            Set<Role> set = stringToRole( request.getAuthorities() );
            if ( set != null ) {
                user.getAuthorities().addAll( set );
            }
        }
        if ( request.getFullName() != null ) {
            user.setFullName( request.getFullName() );
        }
    }
}
