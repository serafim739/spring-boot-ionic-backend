package com.nivaldoSerafim.cursomc.services;

import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nivaldoSerafim.cursomc.domain.ItemPedido;
import com.nivaldoSerafim.cursomc.domain.PagamentoComBoleto;
import com.nivaldoSerafim.cursomc.domain.Pedido;
import com.nivaldoSerafim.cursomc.domain.enums.EstadoPagamento;
import com.nivaldoSerafim.cursomc.repositories.PagamentoRepository;
import com.nivaldoSerafim.cursomc.repositories.PedidoRepository;
import com.nivaldoSerafim.cursomc.repositories.ProdutoRepository;
import com.nivaldoSerafim.cursomc.services.exceptions.ObjectNotFoundException;

@Service
public class PedidoService {
	
	@Autowired
	private PedidoRepository repo;
	
	@Autowired
	private BoletoService boletoService;
	
	@Autowired
	private PagamentoRepository pagamentoRepository;
	
	@Autowired
	private ProdutoRepository produtoRepository;
	
	public Pedido find(Integer id) {
		Optional<Pedido> obj = repo.findById(id);
		return obj.orElseThrow(() -> new ObjectNotFoundException(
				"Objeto não encontrado! Id: " + id + ", Tipo: " + Pedido.class.getName()));
	}
	
	public Pedido insert(Pedido obj) {
		obj.setId(null);
		obj.setInstante(new Date());
		obj.getPagamento().setEstado(EstadoPagamento.PENDENTE);
		obj.getPagamento().setPedido(obj);
		if(obj.getPagamento() instanceof PagamentoComBoleto) {
			PagamentoComBoleto pagto = (PagamentoComBoleto) obj.getPagamento();
			boletoService.preencherPagamentoComBoleto(pagto, obj.getInstante());
		}
		obj = repo.save(obj);
		pagamentoRepository.save(obj.getPagamento());
		for(ItemPedido ip : obj.getItens()) {
			ip.setDesconto(0.0);			
		}
		return obj;
	}
}
