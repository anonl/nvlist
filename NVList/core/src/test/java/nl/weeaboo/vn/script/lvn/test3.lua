
paragraph.start("test", 1); paragraph.append("Simple text"); paragraph.finish()

paragraph.start("test", 2); paragraph.append("Text with\tescape\ncodes[ ] \\"); paragraph.finish()

paragraph.start("test", 3); paragraph.append("Text with \"quotes\""); paragraph.finish()
paragraph.start("test", 4); paragraph.append("Text with "); paragraph.stringify("stringifier"); paragraph.append("and "); paragraph.stringify("longStringifier"); paragraph.append(" embedded."); paragraph.finish()
paragraph.start("test", 5); paragraph.append("Text with "); embedded(); paragraph.append(" code"); paragraph.finish()
paragraph.start("test", 6); paragraph.append("Text with "); embedded("["); paragraph.append(" code"); paragraph.finish()
paragraph.start("test", 7); paragraph.append("Text with {tag a,b,c,d} embedded {/tag} text tags"); paragraph.finish()








text("Code line")


text("Multi")
text("Code")
text("Lines")


