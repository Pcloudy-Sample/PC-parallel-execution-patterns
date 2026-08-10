# Contributing

Thanks for contributing to pCloudy parallel execution patterns.

## Workflow

1. Fork and clone the repository.
2. Create a branch with a clear scope, for example `feat/pattern-04-selenium-sample`.
3. Keep commits focused and small.
4. Open a pull request against the `development` branch with:
   - Problem statement
   - Pattern(s) updated
   - Validation evidence (command output or screenshots)

## Template standards

- Keep credentials out of code. Use environment variables and CI secrets.
- Keep sample tests deterministic and stateless.
- Match naming across frameworks (`auth`, `payments`, `profile`, `search`, `checkout`).
- Any new pattern config must include a runnable sample test set.

## Validation checklist

- Java modules: `mvn -DskipTests test` from the framework folder.
- Node modules: `npm install` then run the documented `npm test` command.
- Tools scripts: run with `--help` and one realistic example input.

## Security

Never commit API keys or uploaded app URLs containing sensitive tokens.
