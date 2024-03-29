package pl.chief.cookbook.security;

import com.vaadin.flow.server.ServletHelper.RequestType;
import com.vaadin.flow.shared.ApplicationConstants;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import pl.chief.cookbook.gui.security.UserAccess;
import pl.chief.cookbook.gui.views.*;
import pl.chief.cookbook.model.Ingredient;
import pl.chief.cookbook.model.Recipe;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public final class SecurityUtils {

    private SecurityUtils() {
        // Util methods only
    }

    static boolean isFrameworkInternalRequest(HttpServletRequest request) {
        final String parameterValue = request.getParameter(ApplicationConstants.REQUEST_TYPE_PARAMETER);
        return parameterValue != null
                && Stream.of(RequestType.values()).anyMatch(r -> r.getIdentifier().equals(parameterValue));
    }

    static boolean isConfirmationTokenTrial(HttpServletRequest request) {
        return request.getRequestURL().toString().contains(ConfirmationView.ROUTE)
                || request.getRequestURL().toString().contains(Confirmation.ROUTE);
    }


    static boolean isUserLoggedIn() {
        return isUserLoggedIn(SecurityContextHolder.getContext().getAuthentication());
    }

    static boolean isUserLoggedIn(Authentication authentication) {
        return authentication != null
                && !(authentication instanceof AnonymousAuthenticationToken);
    }

    public static boolean isAccessGranted(Class<?> securedClass) {
        final boolean publicView = LoginView.class.equals(securedClass)
                || RecipeBrowserView.class.equals(securedClass)
                || Registration.class.equals(securedClass)
                || ConfirmationView.class.equals(securedClass)
                || Confirmation.class.equals(securedClass);

        if (publicView)
            return true;

        Authentication userAuthentication = SecurityContextHolder.getContext().getAuthentication();
        if (!isUserLoggedIn(userAuthentication)) {
            return false;
        }
        // Allow if no roles are required.
        Secured secured = AnnotationUtils.findAnnotation(securedClass, Secured.class);
        if (secured == null) {
            return true;
        }

        List<String> allowedRoles = Arrays.asList(secured.value());
        return userAuthentication.getAuthorities().stream().map(GrantedAuthority::getAuthority)
                .anyMatch(allowedRoles::contains);
    }

    public static boolean isEditingAllowed(Object entity) {
        if (UserAccess.loggedUserRole().equals("ROLE_ADMIN"))
            return true;
        int entityUserId = 0;
        if (entity instanceof Ingredient) {
            entityUserId = ((Ingredient) entity).getUser_id();
        }
        if (entity instanceof Recipe) {
            entityUserId = ((Recipe) entity).getUser_id();
        }
        return entityUserId == UserAccess.loggedUserId();
    }

}