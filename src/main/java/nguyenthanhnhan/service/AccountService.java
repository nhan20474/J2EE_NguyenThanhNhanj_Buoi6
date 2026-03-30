package nguyenthanhnhan.service;

import nguyenthanhnhan.entity.Account;
import nguyenthanhnhan.entity.Role;
import nguyenthanhnhan.repository.AccountRepository;
import nguyenthanhnhan.repository.RoleRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AccountService implements UserDetailsService {

    private final AccountRepository accountRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public AccountService(AccountRepository accountRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.accountRepository = accountRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = accountRepository.findByLoginName(username)
                .orElseThrow(() -> new UsernameNotFoundException("Account not found: " + username));

        Set<GrantedAuthority> authorities = account.getRoles().stream()
                .map(role -> role.getName().startsWith("ROLE_") ? role.getName() : "ROLE_" + role.getName())
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());

        return new User(account.getLoginName(), account.getPassword(), authorities);
    }

    public void registerUser(String loginName, String rawPassword) {
        String normalizedLoginName = loginName == null ? "" : loginName.trim();
        if (normalizedLoginName.isEmpty()) {
            throw new IllegalArgumentException("Tên đăng nhập không hợp lệ");
        }

        if (accountRepository.findByLoginName(normalizedLoginName).isPresent()) {
            throw new IllegalArgumentException("Tên đăng nhập đã tồn tại");
        }

        Role userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new IllegalStateException("Role USER chưa được khởi tạo"));

        Account account = new Account();
        account.setLoginName(normalizedLoginName);
        account.setPassword(passwordEncoder.encode(rawPassword));
        account.setRoles(new HashSet<>(Set.of(userRole)));
        accountRepository.save(account);
    }
}
