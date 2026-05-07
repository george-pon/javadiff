#
# コマンド単位のテスト
#

function dispHeader {
    $msg, $args = $args
    write-host ""
    write-host ""
    write-host ""
    write-host "###  $msg"
    write-host ""
}

function testRun {


    dispHeader "TEST CASE 01 オプション無しヘルプ表示 終了コード:0"
    java -jar ../target/javadiff-jar-with-dependencies.jar
    $RC = $LASTEXITCODE
    write-host "LASTEXITCODE: $LASTEXITCODE"
    if ( $RC -ne 0 ) {
        write-host "exit code error : LASTEXITCODE: $LASTEXITCODE"
        return
    }



    dispHeader "TEST CASE 02 内容が同じファイル比較 終了コード:0"
    java -jar ../target/javadiff-jar-with-dependencies.jar  file01.txt  file02.txt
    $RC = $LASTEXITCODE
    write-host "LASTEXITCODE: $LASTEXITCODE"
    if ( $RC -ne 0 ) {
        write-host "exit code error : LASTEXITCODE: $LASTEXITCODE"
        return
    }



    dispHeader "TEST CASE 03 内容が異なるファイル比較 終了コード:1"
    java -jar ../target/javadiff-jar-with-dependencies.jar  file01.txt  file03.txt
    $RC = $LASTEXITCODE
    write-host "LASTEXITCODE: $LASTEXITCODE"
    if ( $RC -ne 1 ) {
        write-host "exit code error : LASTEXITCODE: $LASTEXITCODE"
        return
    }

}

testRun

