package nguyenthanhnhan.config;

import nguyenthanhnhan.entity.Account;
import nguyenthanhnhan.entity.Role;
import nguyenthanhnhan.repository.AccountRepository;
import nguyenthanhnhan.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Set;

@Configuration
public class SecurityDataInitializer {

    @Bean
    CommandLineRunner initSecurityData(
            AccountRepository accountRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder
    ) {
        return args -> {
            Role adminRole = roleRepository.findByName("ADMIN")
                    .orElseGet(() -> roleRepository.save(new Role(null, "ADMIN")));
            Role userRole = roleRepository.findByName("USER")
                    .orElseGet(() -> roleRepository.save(new Role(null, "USER")));

            if (accountRepository.findByLoginName("admin").isEmpty()) {
                Account admin = new Account();
                admin.setLoginName("admin");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setRoles(new HashSet<>(Set.of(adminRole)));
                accountRepository.save(admin);
            }

            if (accountRepository.findByLoginName("user").isEmpty()) {
                Account user = new Account();
                user.setLoginName("user");
                user.setPassword(passwordEncoder.encode("123456"));
                user.setRoles(new HashSet<>(Set.of(userRole)));
                accountRepository.save(user);
            }
        };
    }
}
