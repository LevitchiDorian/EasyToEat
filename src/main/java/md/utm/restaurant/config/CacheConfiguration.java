package md.utm.restaurant.config;

import java.time.Duration;
import org.ehcache.config.builders.*;
import org.ehcache.jsr107.Eh107Configuration;
import org.hibernate.cache.jcache.ConfigSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.cache.JCacheManagerCustomizer;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.boot.info.BuildProperties;
import org.springframework.boot.info.GitProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.*;
import tech.jhipster.config.JHipsterProperties;
import tech.jhipster.config.cache.PrefixedKeyGenerator;

@Configuration
@EnableCaching
public class CacheConfiguration {

    private GitProperties gitProperties;
    private BuildProperties buildProperties;
    private final javax.cache.configuration.Configuration<Object, Object> jcacheConfiguration;

    public CacheConfiguration(JHipsterProperties jHipsterProperties) {
        JHipsterProperties.Cache.Ehcache ehcache = jHipsterProperties.getCache().getEhcache();

        jcacheConfiguration = Eh107Configuration.fromEhcacheCacheConfiguration(
            CacheConfigurationBuilder.newCacheConfigurationBuilder(
                Object.class,
                Object.class,
                ResourcePoolsBuilder.heap(ehcache.getMaxEntries())
            )
                .withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofSeconds(ehcache.getTimeToLiveSeconds())))
                .build()
        );
    }

    @Bean
    public HibernatePropertiesCustomizer hibernatePropertiesCustomizer(javax.cache.CacheManager cacheManager) {
        return hibernateProperties -> hibernateProperties.put(ConfigSettings.CACHE_MANAGER, cacheManager);
    }

    @Bean
    public JCacheManagerCustomizer cacheManagerCustomizer() {
        return cm -> {
            createCache(cm, md.utm.restaurant.repository.UserRepository.USERS_BY_LOGIN_CACHE);
            createCache(cm, md.utm.restaurant.repository.UserRepository.USERS_BY_EMAIL_CACHE);
            createCache(cm, md.utm.restaurant.domain.User.class.getName());
            createCache(cm, md.utm.restaurant.domain.Authority.class.getName());
            createCache(cm, md.utm.restaurant.domain.User.class.getName() + ".authorities");
            createCache(cm, md.utm.restaurant.domain.Brand.class.getName());
            createCache(cm, md.utm.restaurant.domain.Brand.class.getName() + ".locations");
            createCache(cm, md.utm.restaurant.domain.Brand.class.getName() + ".categories");
            createCache(cm, md.utm.restaurant.domain.Brand.class.getName() + ".promotions");
            createCache(cm, md.utm.restaurant.domain.Location.class.getName());
            createCache(cm, md.utm.restaurant.domain.Location.class.getName() + ".hours");
            createCache(cm, md.utm.restaurant.domain.Location.class.getName() + ".rooms");
            createCache(cm, md.utm.restaurant.domain.Location.class.getName() + ".localPromotions");
            createCache(cm, md.utm.restaurant.domain.Location.class.getName() + ".menuOverrides");
            createCache(cm, md.utm.restaurant.domain.LocationHours.class.getName());
            createCache(cm, md.utm.restaurant.domain.DiningRoom.class.getName());
            createCache(cm, md.utm.restaurant.domain.DiningRoom.class.getName() + ".tables");
            createCache(cm, md.utm.restaurant.domain.RestaurantTable.class.getName());
            createCache(cm, md.utm.restaurant.domain.UserProfile.class.getName());
            createCache(cm, md.utm.restaurant.domain.MenuCategory.class.getName());
            createCache(cm, md.utm.restaurant.domain.MenuCategory.class.getName() + ".items");
            createCache(cm, md.utm.restaurant.domain.MenuItem.class.getName());
            createCache(cm, md.utm.restaurant.domain.MenuItem.class.getName() + ".allergens");
            createCache(cm, md.utm.restaurant.domain.MenuItem.class.getName() + ".options");
            createCache(cm, md.utm.restaurant.domain.LocationMenuItemOverride.class.getName());
            createCache(cm, md.utm.restaurant.domain.MenuItemAllergen.class.getName());
            createCache(cm, md.utm.restaurant.domain.MenuItemOption.class.getName());
            createCache(cm, md.utm.restaurant.domain.MenuItemOption.class.getName() + ".values");
            createCache(cm, md.utm.restaurant.domain.MenuItemOptionValue.class.getName());
            createCache(cm, md.utm.restaurant.domain.Reservation.class.getName());
            createCache(cm, md.utm.restaurant.domain.Reservation.class.getName() + ".tableAssignments");
            createCache(cm, md.utm.restaurant.domain.Reservation.class.getName() + ".orders");
            createCache(cm, md.utm.restaurant.domain.ReservationTable.class.getName());
            createCache(cm, md.utm.restaurant.domain.WaitingList.class.getName());
            createCache(cm, md.utm.restaurant.domain.RestaurantOrder.class.getName());
            createCache(cm, md.utm.restaurant.domain.RestaurantOrder.class.getName() + ".items");
            createCache(cm, md.utm.restaurant.domain.RestaurantOrder.class.getName() + ".payments");
            createCache(cm, md.utm.restaurant.domain.OrderItem.class.getName());
            createCache(cm, md.utm.restaurant.domain.OrderItem.class.getName() + ".optionSelections");
            createCache(cm, md.utm.restaurant.domain.OrderItemOptionSelection.class.getName());
            createCache(cm, md.utm.restaurant.domain.Payment.class.getName());
            createCache(cm, md.utm.restaurant.domain.Review.class.getName());
            createCache(cm, md.utm.restaurant.domain.Notification.class.getName());
            createCache(cm, md.utm.restaurant.domain.Promotion.class.getName());
            // jhipster-needle-ehcache-add-entry
        };
    }

    private void createCache(javax.cache.CacheManager cm, String cacheName) {
        javax.cache.Cache<Object, Object> cache = cm.getCache(cacheName);
        if (cache != null) {
            cache.clear();
        } else {
            cm.createCache(cacheName, jcacheConfiguration);
        }
    }

    @Autowired(required = false)
    public void setGitProperties(GitProperties gitProperties) {
        this.gitProperties = gitProperties;
    }

    @Autowired(required = false)
    public void setBuildProperties(BuildProperties buildProperties) {
        this.buildProperties = buildProperties;
    }

    @Bean
    public KeyGenerator keyGenerator() {
        return new PrefixedKeyGenerator(this.gitProperties, this.buildProperties);
    }
}
