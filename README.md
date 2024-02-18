# SQLDoc

### The problem

I was working on a project with a lot of `.sql` files and trying to keep up with some Markdown documentation at the same time. I was getting annoyed by constantly having to update the Markdown file when I made a change in the SQL, so I decided to make a program that could do it for me.

## Installation

> NOTE: There is currently no way to install SQLDoc automatically. I will be working on an installer for this in the future. For now, follow these steps:

1. Download the `sqldoc.exe` file from this repo. Make sure to put it somewhere you'll remember
2. Environment Variables:
   1. Add an environment variable called `SQLDOC_HOME` that points to the directory where you installed SQLDoc
   2. *(Optional)* Add the directory where you installed SQLDoc to your `PATH` environment variable. This is only necessary if you do not wish to fully qualify the `.exe` path every time you run SQLDoc
4. Try to run `sqldoc -v` or `sqldoc -version` (replace `sqldoc` with the path to the executable if you skipped step 2.ii). If you get a version number, you've successfully installed SQLDoc!

## Usage

To run SQLDoc in your current working directory, simply run `sqldoc` in your terminal. This will use the [default settings](#default-settings) for SQLDoc

### Arguments

There are a variety of arguments that can be passed to the program when the command is executed.

| Argument | Value | Description | Note |
| --- | --- | --- | --- |
| [path] | Valid path or file | Run sqldoc explicitly in a specific directory. | Must be the first argument |
| -md | *NONE* | Specifies output to a markdown file. | |
| -c | *NONE* | Specifies output to the terminal. | |
| -q(uiet) | *NONE* | Will run program without logging to the console | |
| -o(ut) | File name or path | Output to a specific file. | |
| -t(itle) | Title for document | Specify a title for the output document if applicable. | |

### Examples:

```console
sqldoc . -md -o filename.md -title Example Title
sqldoc -md -q
sqldoc -c
```

## Settings

SQLDoc settings can be shown using the following command:

```console
sqldoc settings
```

### Default Settings

See [updating settings](#updating-settings)

| Setting | Value | Valid Values | Description |
| --- | --- | --- | --- |
| output | md | `c`, `md` | Controls how the program produces output. |
| filename | sqldoc | Anything* | Defines the name of the produced file. |
| title | SQLDoc Generated Documentation | Anything** | Defines the title at the top of any produced output files. |

* *No check exists right now for a valid file name. Be careful when setting this option. Invalid values could cause unexpected errors.
* **Spaces should be replaced by `+`. To include a `+` in your title, escape it using `\+`

### Updating Settings

To update a setting, use the following syntax:

```console
sqldoc set <setting>=<value> <setting>=<value>...
```

Example:

```console
sqldoc set title=Example+Title
```

When a setting is updated, you will see a message like:

`set title to New Title`

If there is a problem updating a setting, you will get an error message.

### Resetting Settings

To reset all settings to their default, run:

```console
sqldoc reset
```

You will be prompted to confirm your choice before the settings are reset. **This operation is final. You cannot restore your settings after running this command**
