package br.com.renan.vendas.online.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.renan.vendas.online.domain.Produto;

@Repository
public interface IProdutoRepository extends JpaRepository<Produto, Long> {

        Optional<Produto> findByCodigo(String codigo);

}
