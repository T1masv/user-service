package com.example.subsavvy.Service;

import com.example.subsavvy.Data.User;
import com.example.subsavvy.Repository.UserRepository;
import com.example.subsavvy.Data.Subscription;
import com.example.subsavvy.dto.SubscriptionDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import java.util.List;
import java.util.UUID;
import java.util.Arrays;
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RestTemplate restTemplate;  // Injection du RestTemplate

    // URL de votre microservice Subscription
    private final String subscriptionServiceUrl = "http://localhost:8080/subscriptions";

    public UserService(UserRepository userRepository, RestTemplate restTemplate) {
        this.userRepository = userRepository;
        this.restTemplate = restTemplate;
    }

    // Méthode pour récupérer tous les utilisateurs
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Méthode pour récupérer un utilisateur par son ID
    public User getUserById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    // Méthode pour récupérer tous les abonnements associés à un utilisateur
    public List<Subscription> getSubscriptionsForUser(UUID userId) {
        String url = subscriptionServiceUrl + "/" + userId;  // Construire l'URL pour l'API Subscription
        Subscription[] response = restTemplate.getForObject(url, Subscription[].class);
        return Arrays.asList(response);  // Retourner la liste des abonnements
    }

    // Méthode pour ajouter un abonnement pour un utilisateur
    public Subscription addSubscription(UUID userId, SubscriptionDto subscriptionDto) {
        String url = subscriptionServiceUrl + "/" + userId;  // Construire l'URL pour l'API Subscription
        ResponseEntity<Subscription> response = restTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(subscriptionDto), Subscription.class);
        return response.getBody();  // Retourner l'abonnement ajouté
    }

    // Méthode pour mettre à jour un abonnement pour un utilisateur
    public Subscription updateSubscription(UUID userId, UUID subscriptionId, Subscription subscription) {
        String url = subscriptionServiceUrl + "/" + userId + "/" + subscriptionId;  // Construire l'URL pour l'API Subscription
        ResponseEntity<Subscription> response = restTemplate.exchange(url, HttpMethod.PUT, new HttpEntity<>(subscription), Subscription.class);
        return response.getBody();  // Retourner l'abonnement mis à jour
    }

    // Méthode pour supprimer un abonnement pour un utilisateur
    public void deleteSubscription(UUID userId, UUID subscriptionId) {
        String url = subscriptionServiceUrl + "/" + userId + "/" + subscriptionId;  // Construire l'URL pour l'API Subscription
        restTemplate.exchange(url, HttpMethod.DELETE, null, Void.class);  // Appel DELETE
    }

    // Méthodes CRUD pour l'utilisateur
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public void deleteUser(UUID user) {
        userRepository.deleteById(user);
    }

    public User addUser(String name, String mail, String password_hash, String profile_picture, boolean admin) {
        User user = new User(name, mail, password_hash, profile_picture, admin);
        return userRepository.save(user);
    }

    public User updateUser(UUID id, String name, String mail, String passwordHash) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        existingUser.setName(name);
        existingUser.setMail(mail);
        existingUser.setPassword(passwordHash);

        return userRepository.save(existingUser);
    }

    public User findByUsername(String username) {
        return userRepository.findByName(username);
    }

    public User getUserByMail(String mail) {
        return userRepository.findByMail(mail);
    }
}
