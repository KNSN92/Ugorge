# Ugorge

<p>
  <img alt="適当なサムネ" src="https://raw.githubusercontent.com/KNSN92/Ugorge/refs/heads/main/thumbnail.png" width="50%" />
</p>

このMinecraftのmodは、1.7.10上のUgoCraftと呼ばれる古のmodを、forgeを導入した環境で動作させることが出来るmodです。\
**※※このmodに起因するバグを報告してしまう可能性があるため、決してmao氏のページでバグ報告を行わないで下さい。このリポジトリのIssueに立ててください。※※**

## 動作環境
 - Minecraft: 1.7.10
 - forge: 1.7.10-10.13.4.1614
 - ugocraft: 1.7.10 2.3.0

## 導入方法
 - このmodのjarファイルと別途ダウンロードしてきたUgoCraftのjarファイルをmodsフォルダに入れてください。
 - 起動すればUgoCraftが導入されたMinecraftが起動する筈です〜

## 既知の不具合
 - EntityCulling modを導入すると小さめなUgoObjectがカリングされて見えなくなる

## 今後やりたいこと
- パッチを当てたUgoCraftのjarファイルをキャッシュして、2回目以降の起動を高速化する設定を追加
  - 既に結構早いけどね〜
- 他のmodで追加されたブロックをUgoObject化させるようにするための設定を追加
  - 結構ブロック毎にハードコードされた部分とかがあるから全ブロック対応とかは難しそう...\
    やっぱValkyrien Skiesって凄かったんだなって

## 注意事項
このmodはまだ不安定で、開発をMacOSで行っているため、Windowsだと挙動が異なる可能性もあります。\
そのため、上記の手順を踏んで解決しない場合は、Issueを立ててバグを報告してもらうと今後の改善に繋がります。\
その際は、自身のOS等や他の導入mod等の環境と起動時のログを貼ってもらえると助かります。