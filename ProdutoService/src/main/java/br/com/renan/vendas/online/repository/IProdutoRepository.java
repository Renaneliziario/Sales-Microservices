package br.com.renan.vendas.online.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import br.com.renan.vendas.online.domain.Produto;

@Repository
public interface IProdutoRepository extends MongoRepository<Produto, String> {

	Optional<Produto> findByCodigo(String codigo);

}
