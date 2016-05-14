#### Exemplo de utilização

# Ex: Integração de servidores com atualização de estado incremental e total

1) iniciar primeiro servidor S1
> start 1

2) correr teste TestMovOC

3) juntar um novo servidor S2 (pede o estado total)
> join 2

4) correr teste TestMovOC

5) terminar servidor S1

6) correr teste TestMovOC

7) recuperar o servidor S1 (pede atualizações feitas depois do passo 5) )
> recover 1

(...)


#Nota: testes unitários não servem para garantir que a integraçao de outros servidores está correta (embora ajude a confirmar)
#Nota: no exemplo anterior se não fosse feito o passo 4) o servidor S2 não teria como responder ao pedido de estado de S1,
uma vez que o seu estado foi persistido de porma total (recebeu o estado e não operações) e não teria em memória a
última operação feita por S1.. A resposta de estado (incremental) teria de ser dada por outro servidor que estivesse eventualmente a executar...
