package com.designwright.discord.mudbot.data.persistence.respository;

import com.designwright.discord.mudbot.core.exception.PersistenceException;
import com.designwright.discord.mudbot.core.exception.ResourceNotFoundException;
import com.designwright.discord.mudbot.data.persistence.entity.IdentifiableEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@NoRepositoryBean
public interface BaseRepository<T extends IdentifiableEntity<I>, I> extends JpaRepository<T, I> {

    default T getById(I id) {
        Optional<T> optional = findById(id);

        if (!optional.isPresent()) {
            throw new ResourceNotFoundException();
        }

        return optional.get();
    }

    default List<T> createAll(List<T> entities) {
        return entities
                .stream()
                .map(this::create)
                .collect(Collectors.toList());
    }

    default T create(T entity) {
        if (
                entity.getId() != null
                        && existsById(entity.getId())
        ) {
            throw new PersistenceException("Entry already exists by that ID");
        }

        return save(entity);
    }

    default List<T> updateAll(List<T> entities) {
        return entities
                .stream()
                .map(this::update)
                .collect(Collectors.toList());
    }

    default T update(T entity) {
        if (entity.getId() == null) {
            throw new PersistenceException("Can not update entry without a valid ID");
        } else {
            if (!existsById(entity.getId())) {
                throw new ResourceNotFoundException();
            }
        }

        return save(entity);
    }

}
