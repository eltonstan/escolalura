package br.com.alura.escolalura.codecs;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bson.BsonReader;
import org.bson.BsonString;
import org.bson.BsonValue;
import org.bson.BsonWriter;
import org.bson.Document;
import org.bson.codecs.Codec;
import org.bson.codecs.CollectibleCodec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.types.ObjectId;

import br.com.alura.escolalura.models.Aluno;
import br.com.alura.escolalura.models.Curso;
import br.com.alura.escolalura.models.Habilidade;
import br.com.alura.escolalura.models.Nota;

public class AlunoCodec implements CollectibleCodec<Aluno> {

	private Codec<Document> codec;

	public AlunoCodec(Codec<Document> codec) {
		this.codec = codec;
	}

	@Override
	public void encode(BsonWriter writer, Aluno aluno, EncoderContext encoderContext) {
		Document document = new Document();

		ObjectId id = aluno.getId();
		String nome = aluno.getNome();
		Date dataNascimento = aluno.getDataNascimento();
		Curso curso = aluno.getCurso();
		List<Habilidade> habilidades = aluno.getHabilidades();
		List<Nota> notas = aluno.getNotas();
//		Contato contato = aluno.getContato();

		document.put("_id", id);
		document.put("nome", nome);
		document.put("data_nascimento", dataNascimento);
		document.put("curso", new Document().append("nome", curso.getNome()));

		if (habilidades != null) {
			List<Document> habilidadesDocument = new ArrayList<Document>();
			for (Habilidade habilidade : habilidades) {
				habilidadesDocument.add(
						new Document().append("nome", habilidade.getNome()).append("nível", habilidade.getNivel()));
			}
			document.put("habilidades", habilidadesDocument);
		}

		if (notas != null) {
			List<Double> notasParaSalvar = new ArrayList<>();
			for (Nota nota : notas) {
				notasParaSalvar.add(nota.getValor());
			}
			document.put("notas", notasParaSalvar);
		}

//		List<Double> coordinates = new ArrayList<Double>();
//		for (Double location : contato.getCoordinates()) {
//			coordinates.add(location);
//		}

//		document.put("contato", new Document().append("endereco", contato.getEndereco())
//				.append("coordinates", coordinates).append("type", contato.getType()));

		codec.encode(writer, document, encoderContext);
	}

	@Override
	public Class<Aluno> getEncoderClass() {
		return Aluno.class;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Aluno decode(BsonReader reader, DecoderContext decoderContext) {
		Document document = codec.decode(reader, decoderContext);

		Aluno aluno = new Aluno();

		aluno.setId(document.getObjectId("_id"));
		aluno.setNome(document.getString("nome"));
		aluno.setDataNascimento(document.getDate("data_nascimento"));
		Document curso = (Document) document.get("curso");
		if (curso != null) {
			String nomeCurso = curso.getString("nome");
			aluno.setCurso(new Curso(nomeCurso));
		}

		List<Double> notas = (List<Double>) document.get("notas");
		if (notas != null) {
			List<Nota> notasDoAluno = new ArrayList<>();
			for (Double nota : notas) {
				notasDoAluno.add(new Nota(nota));
				aluno.setNotas(notasDoAluno);
			}
		}

		List<Document> habilidadesDocument = (List<Document>) document.get("habilidades");
		if (habilidadesDocument != null) {
			List<Habilidade> habilidades = new ArrayList<Habilidade>();
			for (Document documentHabilidade : habilidadesDocument) {
				habilidades.add(
						new Habilidade(documentHabilidade.getString("nome"), documentHabilidade.getString("nível")));
			}
			aluno.setHabilidades(habilidades);
		}

//		Document contato = (Document) document.get("contato");
//		if (contato != null) {
//			String endereco = contato.getString("endereco");
//			List<Double> coordinates = (List<Double>) contato.get("coordinates");
//			aluno.setContato(new Contato(endereco, coordinates));
//		}

		return aluno;
	}

	@Override
	public Aluno generateIdIfAbsentFromDocument(Aluno aluno) {
		return documentHasId(aluno) ? aluno : aluno.criarId();
	}

	@Override
	public boolean documentHasId(Aluno aluno) {
		return aluno.getId() != null;
	}

	@Override
	public BsonValue getDocumentId(Aluno aluno) {
		if (!documentHasId(aluno)) {
			throw new IllegalStateException("O aluno não tem um _id");
		}

		return new BsonString(aluno.getId().toHexString());
	}

}
