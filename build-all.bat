@echo off
setlocal
cd /d "%~dp0"
for %%S in (api-gateway auth-service producto-service colaboracion-service pedido-service) do (
    echo Compilando %%S...
    pushd "%%S"
    call .\mvnw.cmd -s .mvn\settings.xml clean test package
    if errorlevel 1 (
        popd
        echo Fallo la compilacion de %%S.
        exit /b 1
    )
    popd
)
echo Los cinco servicios se compilaron correctamente.
exit /b 0
