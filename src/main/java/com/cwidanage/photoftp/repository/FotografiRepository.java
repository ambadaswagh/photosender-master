package com.cwidanage.photoftp.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import com.cwidanage.photoftp.models.Fotografi;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FotografiRepository extends PagingAndSortingRepository<Fotografi, Integer> {
   Fotografi findOneByid(Integer id);

   @Query("select a from Fotografi a where lower(nome_fotografo) = lower(:name)")
   List<Fotografi> findFirstByNome_fotografoIgnoreCase(@Param("name")String name);

   @Query("select a from Fotografi a where a.username = :username")
   public Optional<Fotografi> findByUsername(@Param("username") String username);
}
