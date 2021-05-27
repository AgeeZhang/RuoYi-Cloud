@echo off
echo.
echo [��Ϣ] ��������
echo.


cd %~dp0
cd ..

set version=1.0.0

echo [��Ϣ] ����[ruoyi-auth:%version%]����
cd ./ruoyi-auth
call docker build -t ruoyi-auth:%version% .

echo [��Ϣ] ����[ruoyi-gateway:%version%]����
cd ../ruoyi-gateway
call docker build -t ruoyi-gateway:%version% .

echo [��Ϣ] ����[ruoyi-monitor:%version%]����
cd ../ruoyi-visual/ruoyi-monitor
call docker build -t ruoyi-monitor:%version% .

cd ../../ruoyi-modules

echo [��Ϣ] ����[ruoyi-file:%version%]����
cd ./ruoyi-file
call docker build -t ruoyi-file:%version% .

echo [��Ϣ] ����[ruoyi-gen:%version%]����
cd ../ruoyi-gen
call docker build -t ruoyi-gen:%version% .

echo [��Ϣ] ����[ruoyi-job:%version%]����
cd ../ruoyi-job
call docker build -t ruoyi-job:%version% .

echo [��Ϣ] ����[ruoyi-system:%version%]����
cd ../ruoyi-system
call docker build -t ruoyi-system:%version% .

pause