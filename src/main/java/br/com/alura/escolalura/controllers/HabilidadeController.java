package br.com.alura.escolalura.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import br.com.alura.escolalura.models.Aluno;
import br.com.alura.escolalura.models.Habilidade;
import br.com.alura.escolalura.repositories.AlunoRepository;

@Controller
public class HabilidadeController {

	@Autowired
	private AlunoRepository repositorio;

	@GetMapping("habilidade/cadastrar/{id}")
	public String cadastrar(@PathVariable String id, Model model) {
		Aluno aluno = repositorio.obterAlunoPor(id);
		model.addAttribute("aluno", aluno);
		model.addAttribute("habilidade", new Habilidade());
		return "habilidade/cadastrar";
	}

	@PostMapping("habilidade/salvar/{id}")
	public String salvar(@PathVariable String id, @ModelAttribute Habilidade habilidade) {
		Aluno aluno = repositorio.obterAlunoPor(id);
		repositorio.salvar(aluno.adiciona(aluno, habilidade));
		return "redirect:/aluno/listar";
	}

}