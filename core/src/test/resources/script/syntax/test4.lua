
text("Simple text", nil, {filename="test", line=1,})

text("Text with\tescape\ncodes[ ] \\", {function()   end, }, {filename="test", line=2,})

text("Text with \"quotes\"", nil, {filename="test", line=3,})
text("Text with $stringifier and ${longStringifier} embedded.", nil, {filename="test", line=4,})
text("Text with [embedded()] code", {function() embedded() end, }, {filename="test", line=5,})
text("Text with [embedded(\"[\")] code", {function() embedded("[") end, }, {filename="test", line=6,})
text("Text with {tag a,b,c,d} embedded {/tag} text tags", nil, {filename="test", line=7,})








text("Code line")


text("Multi")
text("Code")
text("Lines")


