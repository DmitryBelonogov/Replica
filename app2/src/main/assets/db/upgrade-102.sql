ALTER TABLE notes RENAME TO notes_tmp;
CREATE
	TABLE notes
	(
        creation INTEGER PRIMARY KEY,
        modification INTEGER,
        title TEXT,
        content TEXT,
        category TEXT,
        task INTEGER,
        done INTEGER,
        archive INTEGER,
        notebook INTEGER,
        saved INTEGER,
        scrollPosition INTEGER
	);
INSERT INTO notes(creation, modification, title, content, category, task, done, archive, notebook, saved,
 scrollPosition)
SELECT creation, modification, title, content, category, task, done, archive, notebook, saved, scrollPosition
FROM notes_tmp;

UPDATE notes

DROP TABLE notes_tmp;