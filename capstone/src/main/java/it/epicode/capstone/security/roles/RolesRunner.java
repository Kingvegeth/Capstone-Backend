package it.epicode.capstone.security.roles;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(6)
public class RolesRunner implements ApplicationRunner {

    @Autowired
    private RolesService rolesService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // Verifica se i ruoli esistono già nel database
        if (!rolesService.existsByRoleType(Roles.ROLES_ADMIN)) {
            rolesService.create(new Roles(Roles.ROLES_ADMIN));
            System.out.println("Ruolo ADMIN inserito");
        } else {
            System.out.println("Ruolo ADMIN già presente");
        }

        if (!rolesService.existsByRoleType(Roles.ROLES_USER)) {
            rolesService.create(new Roles(Roles.ROLES_USER));
            System.out.println("Ruolo USER inserito");
        } else {
            System.out.println("Ruolo USER già presente");
        }
    }
}
