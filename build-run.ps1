#
# コンパイルして実行する
#

. ./build-functions.ps1

f-maven-build

Push-Location .\test
./testRun.ps1
pop-location

