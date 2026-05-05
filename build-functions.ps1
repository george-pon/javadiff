#----------------------------------------------------------------------------
# for maven
#

# mvn ローカルリポジトリを再度ダウンロードしたりリフレッシュしたり
function f-mvn-repo-refresh {
    mvn dependency:purge-local-repository
}

# ビルド番号をインクリメントして得る
function f-get-build-no {
    if (Test-Path ./build-no.ps1) {
        . ./build-no.ps1
    }
    else {
        $buildno = 0
    }
    $buildno++
    Write-Output '$buildno='"$buildno" > ./build-no.ps1
    return $buildno
}

# ビルド番号を読み込む。インクリメントはしない。
function f-read-build-no {
    if (Test-Path ./build-no.ps1) {
        . ./build-no.ps1
    }
    else {
        $buildno = 1
    }
    Write-Output '$buildno='"$buildno" > ./build-no.ps1
    return $buildno
}

function f-maven-build {
    $buildno = f-get-build-no
    Write-Output "buildno is ${buildno}"
    & mvn "-Drevision=${buildno}" clean package
}

# ビルドと起動 (maven)
function f-maven-build-run {
    $buildno = f-get-build-no
    Write-Output "buildno is ${buildno}"
    # ビルド
    & mvn "-Drevision=${buildno}" clean package dependency:copy-dependencies -DincludeScope=runtime
    # テスト実行
    java -jar ./target/javadiff-jar-with-dependencies.jar
    # javadiff
    # javadiff  -body-text  -url http://www.ceres.dti.ne.jp/~george/jindex.html
    # javadiff  -body-text  -url http://blog.livedoor.jp/kaikaihanno/archives/55762990.html
    # $CLASSPATH = "./target/javadiff.jar;./target/dependency/jsoup-1.15.4.jar;."
    # java -classpath "$CLASSPATH" jp.or.rim.yk.george.javadiff.AppMain
    # java -jar ./target/javadiff-1.0-${buildno}-jar-with-dependencies.jar -body-text  -url http://rakukan.net/article/469285083.html
}

# ビルドと起動 (maven)
function f-maven-build-run-fetch {
    $buildno = f-get-build-no
    Write-Output "buildno is ${buildno}"
    & mvn "-Drevision=${buildno}" clean package dependency:copy-dependencies -DincludeScope=runtime
    java -jar ./target/javadiff-1.0.${buildno}-jar-with-dependencies.jar  -new-instance  -disp-url  -lastModified  -body-text  -url http://www.ceres.dti.ne.jp/~george/jdiary_last.html
    # javadiff
    # javadiff  -body-text  -url http://www.ceres.dti.ne.jp/~george/jindex.html
    # javadiff  -body-text  -url http://blog.livedoor.jp/kaikaihanno/archives/55762990.html
    # $CLASSPATH = "./target/javadiff.jar;./target/dependency/jsoup-1.15.4.jar;."
    # java -classpath "$CLASSPATH" jp.or.rim.yk.george.javadiff.AppMain  -new-instance  -disp-url  -lastModified  -body-text  -url http://rakukan.net/article/469285083.html
    # java -classpath "$CLASSPATH" jp.or.rim.yk.george.javadiff.AppMain  -new-instance  -disp-url  -lastModified  -body-text  -url http://www.ceres.dti.ne.jp/~george/jdiary_last.html
    # java -jar ./target/javadiff-1.0-${buildno}-jar-with-dependencies.jar -body-text  -url http://rakukan.net/article/469285083.html
}

# maven で作った javadiff ライブラリを kjwikig にリリースする
function f-maven-build-release {
    Write-Output "copy ./target/javadiff.jar ../kjwikig/lib"
    Copy-Item ./target/javadiff.jar ../kjwikig/lib
}

# maven で作った javadiff ライブラリを freebsd側 にリリースする
function f-maven-build-release-freebsd {
    $buildno = f-read-build-no

    # javadiff シェルの作成。改行コードがCRLFになってしまうのでそこは手動で修正する必要がある。
    $main_str = @"
#!/usr/bin/bash
java -jar ~/bin/javadiff-1.0.${buildno}-jar-with-dependencies.jar "$@"
"@
    $main_file = "./target/javadiff"
    if ( Test-Path $main_file ) {
        Remove-Item $main_file
    }
    Write-Output $main_str | Add-Content -Encoding UTF8 "${main_file}"

    scp ./target/javadiff-1.0.${buildno}-jar-with-dependencies.jar freebsd68:bin
    scp ./target/javadiff freebsd68:bin
}

# 開発開始時、maven build 起動まで実施
function f-maven-setup-all {
    f-maven-build
    if ( $LASTEXITCODE -ne 0 ) { Write-Output "build failed." ; return 1 }

    # run eclipse
    f-eclipse
    Start-Sleep -Milliseconds 10000
    f-wait-for-disk-idle

    # run visual studio code
    code . -r
    Start-Sleep -Milliseconds 10000
    f-wait-for-disk-idle

    f-maven-build-run
    if ( $LASTEXITCODE -ne 0 ) { Write-Output "build and run failed." ; return 1 }
}

#----------------------------------------------------------------------
# gradle build 用 functions 最近は使っていない。
#

# ビルド
function f-gradle-build {
    gradle --warning-mode all     clean build jar distZip
}

# ビルドと起動
function f-gradle-build-run {
    gradle --warning-mode all     clean build jar distZip run
}

# ビルドと起動
function f-gradle-build-run-fetch {
    gradle --warning-mode all     clean build jar distZip run
    if ( Test-Path ./hoge ) {
        Write-Output "./hoge found."
    }
    else {
        New-Item hoge -ItemType Directory
    }
    Push-Location hoge
    unzip  ../build/distributions/javadiff.zip
    Copy-Item javadiff/bin  /home   -Recurse -Force
    Copy-Item javadiff/lib  /home   -Recurse -Force
    Pop-Location
    Remove-Item hoge -Recurse
    # javadiff
    # javadiff  -body-text  -url http://www.ceres.dti.ne.jp/~george/jindex.html
    # javadiff  -body-text  -url http://blog.livedoor.jp/kaikaihanno/archives/55762990.html
    javadiff  -body-text  -url http://rakukan.net/article/469285083.html
}


# ライブラリを追加した場合などに、eclipseの設定ファイルを作り直す
function f-gradle-eclipse-setup {
    gradle --warning-mode all     clean cleanEclipse   eclipse
}

#----------------------------------------------------------------------
# eclipse起動
function f-eclipse {
    Push-Location
    Set-Location C:\HOME\Eclipse-JEE-2026-03-R\eclipse
    Start-Process C:\HOME\Eclipse-JEE-2026-03-R\eclipse\eclipse.exe
    Pop-Location
}

#----------------------------------------------------------------------
# disk の idle percent を取得する
#
function f-getDiskPerf {
    $max_idle = 0;

    $samplesC = Get-Counter -Counter "\LogicalDisk(c:)\% Disk Time" -SampleInterval 1 -MaxSamples 3;
    $idleC = $samplesC.CounterSamples.CookedValue | Measure-Object -Average | Select-Object -ExpandProperty Average;
    Write-Output "idleC : $idleC"
    if ( $idleC -gt $max_idle ) {
        $max_idle = $idleC
    }

    $samplesD = Get-Counter -Counter "\LogicalDisk(d:)\% Disk Time" -SampleInterval 1 -MaxSamples 3;
    $idleD = $samplesD.CounterSamples.CookedValue | Measure-Object -Average | Select-Object -ExpandProperty Average;
    Write-Output "idleD : $idleD"
    if ( $idleD -gt $max_idle ) {
        $max_idle = $idleD
    }
    Write-Output "max_idle : $max_idle"
    return $max_idle
}

#----------------------------------------------------------------------
# disk が暇になるまで待機する
#
function f-wait-for-disk-idle {

    while ($true) {
        $idle_percent = f-getDiskPerf
        if ($idle_percent -lt 20) {
            return;
        }
    }
}


#----------------------------------------------------------------------
# 開発開始時、ビルド、eclipse設定ファイル生成、gradle tomcat 起動まで実施
# 最近は使っていない
function f-gradle-setup-all {
    f-gradle-build
    if ( $LASTEXITCODE -ne 0 ) { Write-Output "build failed." ; return 1 }
    f-gradle-eclipse-setup
    if ( $LASTEXITCODE -ne 0 ) { Write-Output "build eclipse setup." ; return 1 }

    # run eclipse
    f-eclipse
    Start-Sleep -Milliseconds 10000
    f-wait-for-disk-idle

    # run visual studio code
    code . -r
    Start-Sleep -Milliseconds 10000
    f-wait-for-disk-idle

    f-gradle-build-run
    if ( $LASTEXITCODE -ne 0 ) { Write-Output "build and run failed." ; return 1 }
}

#
# end of file
#
