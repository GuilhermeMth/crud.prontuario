package crud.prontuario.exception;

public class PacienteJaCadastradoException extends RuntimeException{
	private static final long serialVersionUID = 1L;

	public PacienteJaCadastradoException(String mensagem) {
		super(mensagem);
	}
	
}
