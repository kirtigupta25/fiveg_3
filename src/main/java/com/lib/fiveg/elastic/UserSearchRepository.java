package com.lib.fiveg.elastic;

import com.lib.fiveg.entity.User;
import com.lib.fiveg.entity.UserElastic;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserSearchRepository extends ElasticsearchRepository<UserElastic, Long> {
    List<UserElastic> findByUsername(String username);
    List<UserElastic> findByEmail(String email);
}
