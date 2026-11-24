@echo off
setlocal
cd /d "%~dp0"
for %%S in (api-gateway auth-service producto-service colaboracion-service pedido-service) do (
    echo Limpiando %%S...
    pushd "%%S"
    call .\mvnw.cmd -s .mvn\settings.xml clean
    if errorlevel 1 (
        popd
        echo Fallo la limpieza de %%S.
        exit /b 1
    )
    popd
)
echo Los cinco servicios quedaron limpios.
exit /b 0
