package br.com.fiap.restaurantusersapi.infrastructure.adapters.inbound.rest.form;

import br.com.fiap.restaurantusersapi.application.ports.inbound.create.CreateUserInput;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Schema(description = "Payload para criação de usuário.", name = "UserCreateRequest")
public class CustomerUserCreateForm {

    @NotBlank
    @Size(min = 3, max = 120)
    @Schema(example = "Maria Silva")
    private final String name;

    @NotBlank
    @Email
    @Size(min = 5, max = 180)
    @Schema(example = "maria.silva@mail.com")
    private final  String email;

    @NotBlank
    @Size(max = 80)
    @Schema(example = "mariasilva")
    private final String login;

    @NotBlank
    @Schema(example = "SenhaFort3!@#*")
    private final String password;

    @Valid
    @Schema(implementation = AddressForm.class)
    private final AddressForm address;

    public CustomerUserCreateForm(String name, String email, String login, String password, AddressForm address) {
        this.name = name;
        this.email = email;
        this.login = login;
        this.password = password;
        this.address = address;
    }

    public CreateUserInput toCreateUserInput() {
        return toCreateUserInput(Set.of(RoleForm.CUSTOMER));
    }

    public CreateUserInput toCreateUserInput(Set<RoleForm> roles) {
        return new CreateUserInput(
                name,
                email,
                login,
                password,
                address != null ? address.toCreateAddressInput() : null,
                roles.stream().map(RoleForm::toCreateRoleInput).collect(Collectors.toSet())
        );
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public AddressForm getAddress() {
        return address;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (CustomerUserCreateForm) obj;
        return Objects.equals(this.name, that.name) &&
                Objects.equals(this.email, that.email) &&
                Objects.equals(this.login, that.login) &&
                Objects.equals(this.password, that.password) &&
                Objects.equals(this.address, that.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, email, login, password, address);
    }

    @Override
    public String toString() {
        return "CustomerUserCreateForm[" +
                "name=" + name + ", " +
                "email=" + email + ", " +
                "login=" + login + ", " +
                "password=" + password + ", " +
                "address=" + address + ']';
    }

}
